/*
 * Generated Mon Jan 12 10:56:41 PST 2015
 */

	.section ".rodata"
	.align 4
_endl:	.asciz "\n"
_intFmt:	.asciz "%d"
_strFmt:	.asciz "%s"
_boolT:	.asciz "true"
_boolF:	.asciz "false"
arrrray_error:	.asciz "Index value of %d is outside legal range [0,%d).\n"
del_errrror:	.asciz "Attempt to dereference NULL pointer.\n"

	.section ".data"
	.align 4
.globalInit:	.word 0

!IntType@14ae5a5 i
	.section ".data"
	.align 4
i:	.word 0

!BoolType@131245a b
	.section ".data"
	.align 4
b:	.word 0

! ---main---
	.section ".text"
	.align 4
	.global main

main:
	set			SAVE.main, %g1
	save		%sp, %g1, %sp

	set			.globalInit, %l5
	ld			[%l5], %l5
	cmp			%l5, %g0
	bne			.globalFinish
	nop

	set			.globalInit, %l5
	set			1, %l7
	st			%l7, [%l5]
.globalFinish:


!i

	set			0, %l6
	mov			%fp-4, %l3
	st			%l6, [%l3]
!0
	mov			%fp-4, %l2
	ld			[%l2], %i0
	ret
	restore


!return 0

