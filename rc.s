/*
 * Generated Mon Jan 12 12:45:49 PST 2015
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

! ---foo---
	.section ".text"
	.align 4
	.global foo

foo:
	set			SAVE.foo, %g1
	save		%sp, %g1, %sp


! ---foo---
	.section ".text"
	.align 4
	.global foo

foo:
	set			SAVE.foo, %g1
	save		%sp, %g1, %sp

	mov			%fp-4, %l5
	st			%i0, [%l5]
	mov			%fp-8, %l3
	st			%i1, [%l3]
	mov			%fp-12, %l2
	st			%i2, [%l2]
	mov			%fp-16, %l4
	st			%i3, [%l4]

	set			0, %l5
	mov			%fp-20, %l1
	st			%l5, [%l1]
!0
	mov			%fp-20, %l0
	ld			[%l0], %i0
	ret
	restore


!return 0

! ---foo---
	.section ".text"
	.align 4
	.global foo

foo:
	set			SAVE.foo, %g1
	save		%sp, %g1, %sp

	mov			%fp-4, %l4
	st			%i0, [%l4]
	mov			%fp-8, %l3
	st			%i1, [%l3]
	mov			%fp-12, %l5
	st			%i2, [%l5]

! ---main---
	.section ".text"
	.align 4
	.global main

main:
	set			SAVE.main, %g1
	save		%sp, %g1, %sp

	set			.globalInit, %l0
	ld			[%l0], %l0
	cmp			%l0, %g0
	bne			.globalFinish
	nop
	ret
	restore


	SAVE.foo = -(92 + 0) & -8
	ret
	restore


	SAVE.foo = -(92 + 20) & -8
	ret
	restore


	SAVE.foo = -(92 + 12) & -8

	set			.globalInit, %l0
	set			1, %l6
	st			%l6, [%l0]
.globalFinish:


!foo

	.section ".rodata"
	.align 4
_float_.00:	.single 0r.0

	.section ".text"
	.align 4
	set			_float_.00, %l1
	ld			[%l1], %l1
	mov			%fp-4, %l3
	st			%l1, [%l3]
!.0
	set			0, %l5
	mov			%fp-8, %l7
	st			%l5, [%l7]
!0
	.section ".rodata"
	.align 4
_float_.01:	.single 0r.0

	.section ".text"
	.align 4
	set			_float_.01, %l6
	ld			[%l6], %l6
	mov			%fp-12, %l4
	st			%l6, [%l4]
!.0
	set			0, %l3
	mov			%fp-16, %l1
	st			%l3, [%l1]
!0
!foo_float_int_float_int

	.section ".rodata"
	.align 4
_float_.02:	.single 0r.0

	.section ".text"
	.align 4
	set			_float_.02, %l7
	ld			[%l7], %l7
	mov			%fp-20, %l5
	st			%l7, [%l5]
!.0
	set			0, %l4
	mov			%fp-24, %l6
	st			%l4, [%l6]
!0
	set			0, %l1
	mov			%fp-28, %l3
	st			%l1, [%l3]
!0
	set			0, %l5
	mov			%fp-32, %l7
	st			%l5, [%l7]
!0
!Illegal call to overloaded function foo.

	.section ".rodata"
	.align 4
_float_.03:	.single 0r.0

	.section ".text"
	.align 4
	set			_float_.03, %l6
	ld			[%l6], %l6
	mov			%fp-36, %l4
	st			%l6, [%l4]
!.0
	set			0, %l3
	mov			%fp-40, %l1
	st			%l3, [%l1]
!0
	.section ".rodata"
	.align 4
_float_.04:	.single 0r.0

	.section ".text"
	.align 4
	set			_float_.04, %l7
	ld			[%l7], %l7
	mov			%fp-44, %l5
	st			%l7, [%l5]
!.0
!Illegal call to overloaded function foo.

	.section ".rodata"
	.align 4
_float_.05:	.single 0r.0

	.section ".text"
	.align 4
	set			_float_.05, %l4
	ld			[%l4], %l4
	mov			%fp-48, %l6
	st			%l4, [%l6]
!.0
	.section ".rodata"
	.align 4
_float_.06:	.single 0r.0

	.section ".text"
	.align 4
	set			_float_.06, %l1
	ld			[%l1], %l1
	mov			%fp-52, %l3
	st			%l1, [%l3]
!.0
	.section ".rodata"
	.align 4
_float_.07:	.single 0r.0

	.section ".text"
	.align 4
	set			_float_.07, %l5
	ld			[%l5], %l5
	mov			%fp-56, %l7
	st			%l5, [%l7]
!.0
!foo_float_float_float

